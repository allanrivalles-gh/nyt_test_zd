module Fastlane
  module Actions

    class GitNewBranchAction < Action
      def self.run(params)
        branch_name = params[:branch_name]
        from_tag = params[:from_tag]

        if from_tag != nil 
          Actions.sh("git checkout -b #{branch_name} #{from_tag}")
        else
          Actions.sh("git checkout -b #{branch_name}")
        end
      end

      #####################################################
      # @!group Documentation
      #####################################################

      def self.description
      end

      def self.details
      end

      def self.available_options
        [
          FastlaneCore::ConfigItem.new(
            key: :branch_name,
            env_name: "FL_GIT_NEW_BRANCH_BRANCH_NAME",
            description: "Name of branch to create",
            verify_block: proc do |value|
              UI.user_error!("No API token for GitNewBranchAction given, pass using `branch_name: 'name'`") unless (value and not value.empty?)
            end
          ),
          FastlaneCore::ConfigItem.new(
            optional: true,
            key: :from_tag,
            env_name: "FL_GIT_NEW_BRANCH_FROM_TAG",
            description: "Tag of release that needs hotfixing"
          )
        ]
      end

      def self.return_value
      end

      def self.authors
        ["jleyrer"]
      end

      def self.is_supported?(platform)
        [:ios, :mac].include?(platform)
      end
    end
  end
end
