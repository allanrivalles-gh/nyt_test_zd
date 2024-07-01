package com.theathletic.codegen.autokoin

class GraphValidator {

    sealed class Result {
        object Success : Result()
        class Fail(val reason: String) : Result()
    }

    companion object {
        fun validate(graph: Map<String, List<String>>): Result {
            return GraphValidator().validate(graph)
        }
    }

    private enum class NodeState {
        UNCHECKED,
        PENDING,
        CLEARED
    }

    private val graphState = mutableMapOf<String, NodeState>()

    fun validate(graph: Map<String, List<String>>): Result {
        graph.keys.forEach { graphState[it] = NodeState.UNCHECKED }

        fun checkNode(node: String): Result {
            // Circular dependency
            if (graphState[node] == NodeState.PENDING) return Result.Fail("Circular dependency")
            if (graphState[node] == NodeState.CLEARED) return Result.Success

            graphState[node] = NodeState.PENDING

            for (childNode in graph.getOrDefault(node, listOf())) {
                val result = checkNode(childNode)
                if (result != Result.Success) {
                    return result
                }
            }
            graphState[node] = NodeState.CLEARED
            return Result.Success
        }

        for (key in graph.keys) {
            val result = checkNode(key)
            if (result != Result.Success) {
                return result
            }
        }

        return Result.Success
    }
}