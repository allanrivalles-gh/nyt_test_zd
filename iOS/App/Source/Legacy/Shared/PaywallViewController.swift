//
//  PaywallViewController.swift
//  theathletic-ios
//
//  Created by Jan Remes on 09/01/2017.
//  Copyright © 2017 The Athletic. All rights reserved.
//

import PureLayout
import UIKit

enum PaywallType {
    case everyplayer
    case podcasts

    var title: String {
        switch self {
        case .everyplayer:
            return Strings.viewAllYourFavoritePlayers.localized
        case .podcasts:
            return Strings.paywallPodcastsTitle.localized
        }
    }

    var subtitle: String {
        switch self {
        case .everyplayer:
            return Strings.reportCardsPaywallSubtitle.localized
        case .podcasts:
            return Strings.paywalPodcastsSubtitle.localized
        }
    }

    var image: UIImage? {
        switch self {
        case .everyplayer:
            return #imageLiteral(resourceName: "paywall_icon")
        case .podcasts:
            return nil
        }
    }

    var height: CGFloat {
        switch self {
        case .everyplayer:
            return 390
        case .podcasts:
            return 300
        }
    }
}

class PaywallViewController: UIViewController, SubscriptionPlansOpening {

    private var dimmedView: UIView?
    private var paywallView: UIView?
    private var paywallBottom: NSLayoutConstraint?

    var paywallType: PaywallType = .everyplayer

    private var paywallHeight: CGFloat {
        return paywallType.height
    }

    init() {
        super.init(nibName: nil, bundle: nil)

        self.modalTransitionStyle = .crossDissolve
        self.modalPresentationStyle = .overFullScreen
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .clear
        addPaywall()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        animateAppearOfPaywall()
    }

    private func addPaywall() {

        let dimmedView = UIView()
        dimmedView.isHidden = true
        dimmedView.backgroundColor = UIColor.black
        view.addSubview(dimmedView)
        dimmedView.autoPinEdgesToSuperviewEdges()
        dimmedView.isUserInteractionEnabled = true
        dimmedView.addGestureRecognizer(
            UITapGestureRecognizer(target: self, action: #selector(dimmedViewTapped(_:)))
        )

        self.dimmedView = dimmedView

        let contentView = UIView()
        view.addSubview(contentView)
        contentView.autoPinEdge(toSuperviewEdge: .leading)
        contentView.autoPinEdge(toSuperviewEdge: .trailing)

        paywallBottom = contentView.autoPinEdge(toSuperviewEdge: .bottom)
        paywallBottom?.constant = paywallHeight + view.safeAreaInsets.bottom

        contentView.autoSetDimension(.height, toSize: paywallHeight)
        contentView.backgroundColor = .gray80

        let iconImageView = UIImageView(image: paywallType.image)
        contentView.addSubview(iconImageView)
        iconImageView.autoPinEdge(toSuperviewEdge: .top, withInset: 28)
        iconImageView.autoAlignAxis(toSuperviewAxis: .vertical)

        let titleLabel = UILabel()
        titleLabel.textAlignment = .center
        titleLabel.font = Theme.boldAppFontOfSize(24)
        titleLabel.textColor = .white
        titleLabel.text = paywallType.title
        contentView.addSubview(titleLabel)

        if paywallType.image != nil {
            titleLabel.autoPinEdge(.top, to: .bottom, of: iconImageView, withOffset: 27)
        } else {
            titleLabel.autoPinEdge(toSuperviewEdge: .top, withInset: 35)
        }

        titleLabel.autoPinEdge(toSuperviewEdge: .left, withInset: 20)
        titleLabel.autoPinEdge(toSuperviewEdge: .right, withInset: 20)

        let subtitleLabel = UILabel()
        subtitleLabel.textAlignment = .center
        subtitleLabel.numberOfLines = 0
        subtitleLabel.font = Theme.regularAppFontOfSize(16)
        subtitleLabel.textColor = .white
        subtitleLabel.text = paywallType.subtitle
        contentView.addSubview(subtitleLabel)

        subtitleLabel.autoPinEdge(.top, to: .bottom, of: titleLabel, withOffset: 10)
        subtitleLabel.autoPinEdge(toSuperviewEdge: .left, withInset: 20)
        subtitleLabel.autoPinEdge(toSuperviewEdge: .right, withInset: 20)

        let plansButton = BigRedButton()
        plansButton.setTitle(Strings.paywallActionButton.localized, for: .normal)
        contentView.addSubview(plansButton)
        plansButton.addTarget(
            self,
            action: #selector(subscribeButtonTapped(sender:)),
            for: .touchUpInside
        )

        plansButton.autoPinEdge(toSuperviewEdge: .left, withInset: 20)
        plansButton.autoPinEdge(toSuperviewEdge: .right, withInset: 20)
        plansButton.autoPinEdge(.top, to: .bottom, of: subtitleLabel, withOffset: 40)

        self.paywallView = contentView

        contentView.layer.cornerRadius = 12
        contentView.layer.masksToBounds = true
        contentView.clipsToBounds = false

        let bottomMaskView = UIView()
        view.addSubview(bottomMaskView)
        bottomMaskView.backgroundColor = .gray80
        bottomMaskView.autoSetDimension(.height, toSize: 100)
        bottomMaskView.autoPinEdge(.top, to: .bottom, of: contentView, withOffset: -20)
        bottomMaskView.autoPinEdge(.left, to: .left, of: contentView)
        bottomMaskView.autoPinEdge(.right, to: .right, of: contentView)

    }

    func animateAppearOfPaywall() {

        if let dimmedView = self.dimmedView, dimmedView.isHidden {

            dimmedView.isHidden = false
            dimmedView.alpha = 0.0
            dimmedView.isUserInteractionEnabled = false

            UIView.animate(
                withDuration: 0.2,
                animations: {

                    dimmedView.alpha = 0.45

                }
            ) { [weak self] finished in

                delay(
                    0.5,
                    closure: {
                        self?.dimmedView?.isUserInteractionEnabled = true
                    }
                )
                //
                //                let layoutAnimation = POPSpringAnimation(propertyNamed: kPOPLayoutConstraintConstant)
                //                layoutAnimation?.springSpeed = 12
                //                layoutAnimation?.springBounciness = 4
                //                layoutAnimation?.toValue = 0.0
                //                self?.paywallBottom?.pop_add(layoutAnimation, forKey: "bounceAppear")

            }
        }

    }

    // MARK: - User actions

    @objc func dimmedViewTapped(_ sender: Any) {

        paywallBottom?.constant = paywallHeight + view.safeAreaInsets.bottom

        dimmedView?.isUserInteractionEnabled = false
        view.setNeedsLayout()

        UIView.animate(
            withDuration: 0.2,
            animations: {

                self.dimmedView?.alpha = 0.0
                self.view.layoutIfNeeded()

            }
        ) { finished in

            self.dimmedView?.isHidden = true
            self.dismiss(animated: true, completion: nil)
        }
    }

    @objc func subscribeButtonTapped(sender: UIButton) {
        presentPlansScreen(
            from: .undefined,
            deeplink: nil,
            successCompletion: { [weak self] in
                guard let self = self else { return }
                if UserService.shared.isUserAnonymous {
                    self.goToPostSubFlow(self, entryPoint: .inAppFlow)
                } else {
                    if UserDefaults.showAttributionSurvey {
                        self.openSurvey(location: .plansView)
                    }
                }
            }
        )
    }
}

extension PaywallViewController: OnboardingDelegate {
    func loginDidComplete(controller: UIViewController?) {
        controller?.dismiss(
            animated: true,
            completion: { [weak self] in
                guard let self = self else { return }
                if UserDefaults.showAttributionSurvey {
                    self.openSurvey(location: .plansView)
                }
            }
        )
    }
}

extension PaywallViewController: LoginOpening {}
extension PaywallViewController: AttributionSurveyOpening {}
