fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

### generateApollo

```sh
[bundle exec] fastlane generateApollo
```

Generate Apollo code

 - `refresh`: Refresh the schema.


### release_prepare

```sh
[bundle exec] fastlane release_prepare
```

Prepare a production build for release

 - `version`: The version for which a new release is being created.


### hotfix_prepare

```sh
[bundle exec] fastlane hotfix_prepare
```

Prepare a hotfix for release

 - `version`: The version for which a new hotfix is being created.


 - `from_tag`: The tag at which to create the hotfix.


### release_finalize

```sh
[bundle exec] fastlane release_finalize
```

Finalize a release

### distribute_beta

```sh
[bundle exec] fastlane distribute_beta
```

Distribute a target's most recent build to beta testers

 - `targets`: Comma-separated target(s) to distribute for testing.


### release_notes

```sh
[bundle exec] fastlane release_notes
```

Determine the changelog between two git tags. Supplying no tags will draft a changelog from the previous tag.

 - `from_tag`: The starting tag from which to search for commit changes.

 - `to_tag`: The ending tag at which to stop searching for commit changes.

 - `embed_links`: `true` if JIRA links should be added to commit messages. Default is `true`.


### get_compass_config

```sh
[bundle exec] fastlane get_compass_config
```

Fetch current Compass configuration

 - `config`: The file containing URL query parameters. Defaults to `fastlane/actions/compass_query_parameters`.


 - `environment`: The API environment.


### add_feature_flag

```sh
[bundle exec] fastlane add_feature_flag
```

Add feature flag to Compass

 - `json_file`: The JSON file containing feature flag options.
See https://theathletic.atlassian.net/wiki/spaces/ENG/pages/1664811009/Feature+Flags for details.


 - `environment`: The API environment.


### update_feature_flag

```sh
[bundle exec] fastlane update_feature_flag
```

Update Compass feature flag

 - `name`: The name/key of the feature flag to update.


 - `value`: The new value. (optional, remains unchanged if omitted.)


 - `is_live`: Whether or not the feature flag is live. (optional, remains unchanged if omitted.)


 - `environment`: The API environment.


----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
