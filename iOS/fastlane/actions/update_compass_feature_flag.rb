module Fastlane
  module Actions

    class UpdateCompassFeatureFlagAction < Action
      def self.run(params)
        flag_name = params[:name]
        flag_value = params[:value]
        flag_is_live = params[:is_live]
        host = params[:environment]
        auth_token = params[:auth_token]

        https = Net::HTTP.new(host, 443)
        https.use_ssl = true
        path = "/compass/v1/flags"

        headers = {
          "Authorization" => "Bearer #{auth_token}",
          "Content-Type" => "application/json; charset=utf-8"
        }
        
        body = {"key" => flag_name}

        if flag_value != nil
          body["value"] = flag_value
        end 

        if flag_is_live != nil 
          body["is_live"] = flag_is_live
        end 
        
        response = https.put(path, body.to_json, headers)
        puts "Status: #{response.code} #{response.message}"

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
            key: :name,
            env_name: "FL_UPDATE_COMPASS_FF_NAME",
            description: "Name of the existing feature flag",
          ),
          FastlaneCore::ConfigItem.new(
            optional: true,
            key: :value,
            env_name: "FL_UPDATE_COMPASS_FF_VALUE",
            description: "New value for an existing feature flag",
          ),
          FastlaneCore::ConfigItem.new(
            optional: true,
            key: :is_live,
            env_name: "FL_UPDATE_COMPASS_FF_IS_LIVE",
            description: "Toggle existing feature flag on/off",
            is_string: false
          ),
          FastlaneCore::ConfigItem.new(
            key: :environment,
            env_name: "FL_UPDATE_COMPASS_FF_ENVIRONMENT",
            description: "Environment to use for writing feature flag",
          ),
          FastlaneCore::ConfigItem.new(
            key: :auth_token,
            env_name: "FL_UPDATE_COMPASS_FF_AUTH_TOKEN",
            description: "Compass auth token for updating feature flag",
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
