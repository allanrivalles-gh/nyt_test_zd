# The Athletic Coding Guidelines

### Table Of Contents
- [Happy Path Coding](#happy-path-coding)
- [Avoid Pyramid of Doom](#avoid-pyramid-of-doom)
- [Law of Demeter](#law-of-demeter)
- [Reusability](#reusability)
- [File Organization](#file-organization)
- [Line Character Limit](#line-character-limit)
- [Capturing Self in Blocks](#capturing-self-in-blocks)
- [Style](#style)
- [The Athletic Design Theme](#the-athletic-design-theme)
- [Naming](#naming)
- [SwiftUI Modifiers](#swiftui-modifiers)
- [Concurrency](#concurrency)

---

### Happy Path Coding
All code you write should follow happy path coding along the leftmost indentation of a method. This means, when reading through a method, one should be able to read through the code along the starting indentation level and be taken through the most common use case. This may require multiple return statements.

```swift
// DO
func happyPath() {
    guard let height = self.tabBarController?.tabBar.bounds.height else {
        return
    }
    
    self.bottomConstraint.constant -= height
    
    UIView.animate(duration: 0.5) {
        self.view.layoutIfNeeded()
    }
}

// DON'T
func notHappyPath() {
    if let height = self.tabBarController?.tabBar.bounds.height {
        self.bottomConstraint.constant -= height

        UIView.animate(duration: 0.5) {
            self.view.layoutIfNeeded()
        }   
    }
}
```
---

### Avoid Pyramid of Doom
Sometimes you will be incapable of keeping the happy path along the leftmost indentation level. A good rule of thumb is to create a new method to call through to when your code within a method gets more than 3 indentation levels deep.

This code is very hard to read:

```swift
func webViewIntercept(
    _ webView: UIWebView!, 
    shouldStartLoadWithRequest request: URLRequest!, 
    navigationType: UIWebViewNavigationType
) -> Bool {
    if loadStatus == LoadStatus.enabled {
        loadStatus = LoadStatus.loading
        
        NSURLConnection.sendAsynchronousRequest(
            request, 
            queue: OperationQueue()
        ) { (response, data, error) -> Void in
            
            if let res = response {
                let httpResponse: HTTPURLResponse = res as! HTTPURLResponse
                if httpResponse.statusCode >= 400 {
                    self.loadStatus = LoadStatus.enabled
                    self.showErrorForResponseCode(httpResponse.statusCode)
                }
                else {
                    self.loadCount += 1
                    self.loadStatus = LoadStatus.needsDisplay
                    webView.load(data!, mimeType: httpResponse.mimeType!, textEncodingName: httpResponse.textEncodingName!, baseURL: httpResponse.url!)
                    let str = String(data: data!, encoding: String.Encoding.utf8)!
                    if str.contains(
                        "callNativeFunction('setNavigationBar', 
                        ['left', 'Back', 'navigateToSomewhere']
                    );") {
                        foreground {
                            ...
                        }
                    }
                    ...
}
```
---

### Law of Demeter
The Law of Demeter essentially means to avoid accessing a class too deeply and relying on it's implementation to not change. Even more simply, don't use too many . on one line.

`a.methodA()` vs `a.b.c.methodC()`

In the first example, `methodA()` would ultimately call through to `methodC()`. The benefit here is that the caller no longer needs to know about b or c. In case the underlying implementation of a changes to no longer rely on b or c, only `methodA()` will have to be updated rather than everywhere `methodC()` is called the way in the second example.

---

### Reusability
If you have used the same 5+ lines of code in more than 2 places, it's time to start thinking about moving that to a place it can be reused (superclass, extension, etc).

---

### File Organization
Within a file, you should break up collections of methods, vars, etc into logical sections. Examples include lifecycle methods, IBActions, network calls, or notifications. You should further preface each section with the name in the form of `// MARK: - Lifecycle` surrounded by newlines. The ordering of these sections should roughly match the following:

```swift
...
... (properties)
...

// MARK: - Initialization

init() {
    ...
}

// MARK: - Lifecycle

override func viewDidLoad() {
    ...
}

// MARK: - Configuration


func configure(with viewModel: SomeViewModel) {
   ...
}

// MARK: - Additional sections in any order (Actions, Notifications, Services, whatever)

@IBAction func didTapButton(_ sender: UIButton) {
    ...
}
```
Additionally, objects conforming to protocols should do so in extensions if possible. You can typically keep these in the same file. These should also be prefaced with `// MARK: - UICollectionViewDelegate` with a single newline above.

```swift
    ...
}

// MARK: - UICollectionViewDelegate
extension MyViewController: UICollectionViewDelegate {
    ...
}
```

Types/Properties should be declared at the top of the file in the appropriate order based on:

* Types first
* typealias
* struct
* enum
* IBOutlets
* Class Properties
    * Ordered by access scope (most access to least access)
* Instance Properties
    * Ordered by access scope (most access to least access)
    * Constants before variables
    * Stored before computed variables

```swift
class MyViewController: UIViewController {
    typealias CompletionHandler = (Bool) -> Void    

    @IBOutlet private weak var loginButton: UIButton!
    @IBOutlet private weak var registerButton: UIButton!
    @IBOutlet private weak var childContainerView: UIScrollView!
    
    static let someClassProperty: Int = 1
    private static let privateClassProperty: Int = 2
        
    var handler: CompletionHandler?
    weak var delegate: MyViewControllerDelegate?
    
    private let impressionTrackableManager: ImpressionTrackableManager!
    private var someView = UIView(frame: .zero)
    
    // MARK: - Lifecycle
    
    override func viewDidLoad() {
        super.viewDidLoad()

        ...
    }

    // MARK: - Actions

    ...

    // MARK: - Services
    
    ...
```
---

### Line Character Limit
To increase readability, lines should be kept to a limit of 100 characters or less. 

```swift
// DO
private func processHeadlineLayout(
    item: GQL.FeedQuery.Data.AppFeedV2.Item,
    at index: Int,
    snapshot: inout FeedSnapshot
) {
    guard AthRemoteConfig.shared.value(for: .isNSEHeadlinesFeedEnabled),
        let headline = item.asHeadlineLayout?.headline
    else { 
        return 
    }

    let metaBlob = ShuttleAnalyticsEvent.MetaBlob(
        pageOrder: index,
        container: "headline_single"
    )
    
    ...
}

    
// DON'T

private func processHeadlineLayout(item: GQL.FeedQuery.Data.AppFeedV2.Item, at index: Int, snapshot: inout FeedSnapshot) {
    guard AthRemoteConfig.shared.value(for: .isNSEHeadlinesFeedEnabled), let headline = item.asHeadlineLayout?.headline else { return }

    let metaBlob: ShuttleAnalyticsEvent.MetaBlob = .init(pageOrder: index, container: "headline_single")
    
    ...
}

```
---

### Capturing `self` in Blocks
If for any reason there is a chance self can become `nil` when captured in a block, capture it weakly. To aid in this boilerplate, simply unwrap self as soon as possible using a single line guard statement with the variable `self`. 

```swift
// DO
PodcastService.shared.getPodcastEpisodeDetails(episodeId: episodeId) { [weak self] result in
    guard let self = self else { return }
    
    ...
}
```
---

### Style
**Indentation**

* Xcode default of 4 spaces
* Switch case statements should be at the same indentation level as the switch statement they belong to

**Imports**

Cleanup all unused module imports

**Braces**

All braces should be on the same line of code that they follow. The exception to this is multiline initializers/method definitions where the parameter list is too long to fit on one line.

```swift
// DO
override viewDidLoad() {
    super.viewDidLoad()
    
    if someBoolean {
        print("Cool")
    }
}

private func processHeadlineLayout(
    item: GQL.FeedQuery.Data.AppFeedV2.Item,
    at index: Int,
    snapshot: inout FeedSnapshot
) {
    ...
}


// DON'T
override viewDidLoad() 
{
    super.viewDidLoad()
    
    if someBoolean 
    {
        print("Cool")
    }
}
```

**Comments**

Single line comments composed of //, a space, and a comment

```swift
// DO
// this code does cool stuff


// DON'T
//This is nice code
// COOL STUFF GOING ON HERE
```

Multi-line comments should be preceded by `///`, to match Xcode-generated Swift documentation. When commenting methods, use Swift Doc Markup

```swift
/// My Method does something.
/// - Parameters:
///   - item: item description
///   - snapshot: snapshot description
```

**Property Definition**

Properties should always be let unless they intend to be changed. They should include the strictest access specifier as the first word in the definition, and only include an explicit type when it cannot be inferred properly by the compiler. When specifying types, the property name is immediately followed by a :, a space, then the type. 

```swift
// DO
@IBOutlet private weak var bannerView: UIView!

private let viewIdentifier = "viewIdentifier"

let displayType = DisplayTypeEnum.light


// DON'T
@IBOutlet weak private var bannerView : UIView!

private let viewIdentifier :String = "viewIdentifier"

let displayType: DisplayTypeEnum = .light
```

For getter only properties, omit the get keyword.

```swift
// DO
var tabBarHeight: CGFloat {
    return self.tabBarController?.tabBar.bounds.height ?? 0
}


// DON'T
var tabBarHeight: CGFloat {
    get {
        return self.tabBarController?.tabBar.bounds.height ?? 0
    }
}
```

**Constants**

* Define private constants within Swift file for faster access and reusability.

```swift
    private struct Constants {
        static let randomFontSize: CGFloat = 26.0
        static let randomPaddingSize: CGFloat = 14.0
        static let randomUserKey: String = "randomUserKey"
    }
```

**Super Calls**

When a method calls through to it's superclass' implementation and it's the first line of a method, include a newline after the super call. This removes the boilerplate code (super call) from your custom code.

```swift
// DO
override func viewDidLoad() {
    super.viewDidLoad()
    
    insertStatusView()
}

// DON'T
override func viewDidLoad() {
    super.viewDidLoad() 
    insertStatusView()
}
```

**Use of self**

Explicitly writing self is not required in most cases and should be avoided. Instances where self may be required include parameter name conflicts in initalizers and capturing self in a block. Avoid naming conflicts with non-init method parameter names that force the use of self.

By default, you should always capture self weakly in a block unless you are absolutely sure there is no chance of a memory leak.

```swift
// DO
var cellHeight: CGFloat = 100

func update(cellHeight newHeight: CGFloat) {
    cellHeight = newHeight
}

fetchStuff() { [weak self] in
    self?.updateLayout
}


// DON'T
var cellHeight: CGFloat = 100

func update(cellHeight: CGFloat) {
    self.cellHeight = cellHeight
}

fetchStuff() {
    self.updateLayout
}
```

**Blocks**

When declaring blocks, do the following:

Omit parenthesis whenever possible
Short or long-hand notation is acceptable for 1 line blocks
Use long-hand notation (names parameter, newlines) whenever block includes 2 or more lines

```swift
// DO
widgetsToMove.reversed().forEach { widgets.append($0) }

widgetsToMove.reversed().forEach { widget in
    widgets.append(widget)
    ...
}

// DON'T
widgetsToMove.reversed().forEach({ widget.append($0) })

```

**Forced Unwrapped Optionals (!)**

Forced unwrapped optionals should be completely avoided. The benefit of reducing the fragility of your code vastly outweights the few lines saved from a force unwrap. The exceptions to this rule are:

* IBOutlets
* Test classes

```swift
// DO
guard let url = URL(string: "http://somedomain.com") else {
    return
}

ServicesManager.shared.fetch(url)

// DON'T
let url = URL(string: "http://somedomain.com")!

ServicesManager.shared.fetch(url)
```

**Semicolons and Parentheses**

Do not wrap if, while, switch-case, or for variables in parentheses unless required. Additionally, don't use semicolons even though they are valid syntax.

```swift
// DO
if isIphone {
    // do stuff
}

let height = x == 2 ? 100 : 0

switch self {
case .home:
    // code
case .browse:
    // code
}


// DON'T
if(isIphone) {
    // do stuff
}

let height = (x == 2) ? 100 : 0;
```

**Return Statements**

The following rules should apply to return statements

* Return statements are always preceded by a newline unless they are the only line of a method
* Prefer returning short over setting a further unmodified variable to be returned later
* Prefer return the result of an expression when possible rather than setting a variable needlessly

```swift
// DO
func test() -> String {
    let myVariable = "some string"

    return myVariable
}


// DON'T
func test() -> String {
    let myVariable = "some string"
    return myVariable
}
``` 

---

### The Athletic Design Theme

Design specifications can be found on [Figma](https://www.figma.com/file/rqZmS626BLKjhsEnr8b5lx/Product-Design-System-2.0?node-id=249%3A230). Whenever possible, please reference `ATHTheme.swift` for the following:

**Fonts/Style**

```swift
let style = ATHTheme.Headline.extraLargeInline
tagLabel.font = style.font
tagLabel.textColor = style.color
```

**Colors**

```swift
messageInput.textColor = .gray5

```

**UI Dimensions**

```swift
ATHTheme.Dimension.medium.padding

```
---

### Naming
**Properties**

Both class and instance variables should use camelCase for naming. Do not abbreviate variable names unless absolutely necessary. You may remember what the abbreviation means now, but not in a year from now. Keep names meaningful but not overly long. When dealing with acronyms (URL), continue to use camelCase naming.

```swift
// DO
let xOffset = 5
let rootViewController = UIViewController()
let servicesUrl = "https://services.com"

// DON'T
let xo = 5
let rVC = UIViewController()
let servicesURL = "https://services.com"
```

**Methods**

Method names should follow the latest Swift API Design Guidelines. Additionally, @IBActions should take the form of {action}{control}(_ sender: ControlType).

```swift
@IBAction func didTapCheckoutButton(_ sender: UIButton)
@IBAction func didChangeSomeControl(_ sender: UISegmentedControl)
```

**Parameters and Arguments**

Parameter and argument naming should follow [Apple's naming conventions](https://www.swift.org/documentation/api-design-guidelines/#parameter-names). Notably, when the first parameter is forming a prepositional phrase its name should begin with the preposition e.g. `with`, `for`, `in` etc. and must include additional words if the input value would be ambiguous.

The parameter name may just be the preposition if the `type` of the argument disamgiguates the expected input value. When the `type` of the parameter doesn't clearly indicate the expected input value, the `parameter` name must disambiguate it. For example `withCount: Int` rather than `with: Int`, `forUserId: Int` rather than `for: Int`, and `toName: String` rather than `to: String`.

The `argument label` should be supplementary to help with documentation; the function should still be clearly readable if the argument label is omitted.

```
// DO
func deleteUser(withId userId: Int)
func editingRect(forBounds bounds: CGRect) -> CGRect
func makeSections(with articleGroups: [ArticleGroup])

// DO NOT
func deleteUser(with userId: Int)
/// If we omit the argument label, the function would become `func deleteUser(with: Int)` and it is not clear what `with` value we're expected to input. We might assume it is the user ID, but it is not absolutely clear.

func editingRect(for bounds: CGRect) -> CGRect
/// If we omit the argument label, the function would become `func editingRect(for: CGRect) -> CGRect` and at this point it is not clear what we're expected to input for the `for` parameter.

```

---

### SwiftUI Modifiers

**File Naming**

Modifier files should be named according to the modifier they contain.
```
// DO
GetSizeModifier.swift

// DON'T
View+GetSize.swift
GetSize.swift
ViewGetSizeModifier.swift
```

**File Structure**

Modifier files should contain only the relevant modifier and only expose the necessary view extension interface required to use the modifier. _Things that don't need to be public shouldn't be public._
```swift
// DO
extension View {
    func getSize(...) {
        ...
    }
}

private struct GetSizeModifier: ViewModifier {
    ...
}

// DON'T
struct GetSizeModifier: ViewModifier {
    ...
}

extension View {
    func getSize(...) {
        ...
    }
}
```

---

### Concurrency

**Continuations**

We can bridge the gap between `async/await` concurrency and asynchronous closures by using a "continuation".
(`withCheckedContinuation` and `withCheckedThrowingContinuation`). These functions provide a `continuation` instance which you must inform once the asynchronous operation has completed. There are two options for how to resume the continuation:
* `resume(with: Result<T, E>)`
* `resume(returning: T)` and `resume(throwing: E)` pair

The `resume(with:)` throws or returns on your behalf based on whether the result is a `.success` or `.failure`.

When using a continuation you SHOULD be consistent in how the continuation is resumed. If one branch of code is `resume(throwing:)` then the counterpart should `resume(returning:)`.

If however, you simply need to return a `Result` object up the chain without processing it, then you can use `resume(with: Result<T, E>)`

**DO**

```swift
/// If we need to do some additional processing or mapping on the `Result`
await withCheckedThrowingContinuation { continuation in
    doSomething() { result in
        switch result {
        case .success(let data):
            let somethingFromTheData = data.map { MoreData($0) }
            continuation.resume(returning: somethingFromTheData)

        case .failure(let error):
            logTheError(error)
            let customizedError = MyOtherError.someError
            continuation.resume(throwing: customizedError)
        }
    }
}

/// If we simply want to throw or return based on whether the result is a `success` or `failure`
await withCheckedThrowingContinuation { continuation in
    doSomething() { result in
        continuation.resume(with: result)
    }
}
```

**DON'T**

```swift
/// Don't mix the return pattern
await withCheckedThrowingContinuation { continuation in
    doSomething() { result in
        switch result {
        case .success(let data):
            continuation.resume(with: .success(data))

        case .failure(let error):
            logTheError(error)
            let customizedError = MyOtherError.someError
            continuation.resume(throwing: customizedError)
        }
    }
}

/// Don't map the Result into another Result if we don't need to do any mapping on the values
await withCheckedThrowingContinuation { continuation in
    doSomething() { result in
        switch result {
        case .success(let data):
            continuation.resume(returning: data)

        case .failure(let error):
            continuation.resume(throwing: error)
        }
    }
}

```
