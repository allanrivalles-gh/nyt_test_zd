module Fastlane
  module Actions

    class FetchCompassConfigAction < Action
      def self.run(params)
        config_file = params[:config_file]
        host = params[:environment]

        https = Net::HTTP.new(host, 443)
        https.use_ssl = true

        query_params = File.readlines(config_file, chomp: true).join("&")
        path = "/compass/v4/config?#{query_params}"

        response = https.get(path)

        json = JSON.parse(response.body)
        puts "Response Body: #{JSON.pretty_generate(json)}"

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
            key: :config_file,
            env_name: "FL_FETCH_COMPASS_CONFIG_CONFIG_FILE_NAME",
            description: "Name of config file to use for fetch",
          ),
          FastlaneCore::ConfigItem.new(
            key: :environment,
            env_name: "FL_FETCH_COMPASS_CONFIG_ENVIRONMENT",
            description: "Environment to use for fetch",
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
