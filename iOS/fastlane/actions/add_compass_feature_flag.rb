module Fastlane
  module Actions

    class AddCompassFeatureFlagAction < Action
      def self.run(params)
        json_file = params[:json_file]
        host = params[:environment]
        auth_token = params[:auth_token]

        https = Net::HTTP.new(host, 443)
        https.use_ssl = true
        path = "/compass/v1/flags"

        headers = {
          "Authorization" => "Bearer #{auth_token}",
          "Content-Type" => "application/json; charset=utf-8"
        }

        file_data = File.read(json_file)
        data = JSON.parse(file_data)
        response = https.post(path, data.to_json, headers)

        puts "Status: #{response.code} #{response.message}"
        puts response.body

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
            key: :json_file,
            env_name: "FL_ADD_COMPASS_FF_CONFIG_FILE_NAME",
            description: "Name of config file to use for new feature flag creation",
          ),
          FastlaneCore::ConfigItem.new(
            key: :environment,
            env_name: "FL_ADD_COMPASS_FF_ENVIRONMENT",
            description: "Environment to use for adding the feature flag",
          ),
          FastlaneCore::ConfigItem.new(
            key: :auth_token,
            env_name: "FL_ADD_COMPASS_FF_AUTH_TOKEN",
            description: "Compass auth token for adding feature flag",
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
