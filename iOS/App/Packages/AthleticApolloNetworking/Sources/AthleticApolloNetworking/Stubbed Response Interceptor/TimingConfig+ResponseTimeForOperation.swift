//
//  TimingConfig+ResponseTimeForOperation.swift
//
//
//  Created by Mark Corbyn on 6/11/2023.
//

import Foundation

extension TimingConfig {

    func responseTime(
        forOperationName operationName: String,
        sequenceNumber: String?
    ) -> TimeInterval {
        guard let operationConfig = operations[operationName] else {
            /// If there's no config specific to this operation return the default fallback
            return overallFallback
        }

        guard let sequenceNumber else {
            /// If it's not a request sequence, return the fallback time for this operation
            return operationConfig.fallback ?? overallFallback
        }

        guard let sequenceConfig = operationConfig.sequence[sequenceNumber] else {
            /// If there's no config for this sequence, try to return the fallback config for this operation name if available
            return operationConfig.fallback ?? overallFallback
        }

        /// Return the specific time for this operation + sequence number
        return sequenceConfig
    }

    private var overallFallback: TimeInterval {
        fallback ?? 0
    }

}
