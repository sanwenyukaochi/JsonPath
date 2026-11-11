package com.jayway.jsonpath
import com.jayway.jsonpath.internal.filter.FilterCompiler

abstract class Filter : Predicate {
    abstract override fun apply(context: Predicate.PredicateContext): Boolean

    open fun or(other: Predicate): Filter = OrFilter(this, other)

    open fun and(other: Predicate): Filter = AndFilter(this, other)

    private class SingleFilter(
        private val predicate: Predicate,
    ) : Filter() {
        override fun apply(context: Predicate.PredicateContext): Boolean = predicate.apply(context)

        override fun toString(): String {
            val predicateString = predicate.toString()
            return if (predicateString.startsWith("(")) "[?" + predicateString + "]" else "[?(" + predicateString + ")]"
        }
    }

    private class AndFilter(
        private val predicates: Collection<Predicate>,
    ) : Filter() {
        constructor(left: Predicate, right: Predicate) : this(java.util.Arrays.asList(left, right))

        override fun and(other: Predicate): Filter {
            val newPredicates = ArrayList<Predicate>(predicates)
            newPredicates.add(other)
            return AndFilter(newPredicates)
        }

        override fun apply(context: Predicate.PredicateContext): Boolean {
            for (predicate in predicates) if (!predicate.apply(context)) return false
            return true
        }

        override fun toString(): String {
            val i = predicates.iterator()
            val sb = StringBuilder()
            sb.append("[?(")
            while (i.hasNext()) {
                var p = i.next().toString()
                if (p.startsWith("[?(")) p = p.substring(3, p.length - 2)
                sb.append(p)
                if (i.hasNext()) sb.append(" && ")
            }
            sb.append(")]")
            return sb.toString()
        }
    }

    private class OrFilter(
        private val left: Predicate,
        private val right: Predicate,
    ) : Filter() {
        override fun and(other: Predicate): Filter = OrFilter(left, AndFilter(right, other))

        override fun apply(context: Predicate.PredicateContext): Boolean {
            val a = left.apply(context)
            return a || right.apply(context)
        }

        override fun toString(): String {
            val sb = StringBuilder()
            sb.append("[?(")
            var l = left.toString()
            var r = right.toString()
            if (l.startsWith("[?(")) l = l.substring(3, l.length - 2)
            if (r.startsWith("[?(")) r = r.substring(3, r.length - 2)
            sb.append(l).append(" || ").append(r)
            sb.append(")]")
            return sb.toString()
        }
    }

    companion object {
        @JvmStatic fun filter(predicate: Predicate): Filter = SingleFilter(predicate)

        @JvmStatic fun filter(predicates: Collection<@JvmSuppressWildcards Predicate>): Filter = AndFilter(predicates)

        @JvmStatic fun parse(filter: String): Filter = FilterCompiler.compile(filter)
    }
}
