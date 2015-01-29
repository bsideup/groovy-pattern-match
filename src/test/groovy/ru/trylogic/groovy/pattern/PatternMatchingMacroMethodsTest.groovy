package ru.trylogic.groovy.pattern

import groovy.transform.NotYetImplemented

class PatternMatchingMacroMethodsTest extends GroovyTestCase {

    public void testMatchFact() {
        assertScript '''
        def fact(num) {
            return match(num) {
                when String then fact(num.toInteger())
                when 0 or 1 then 1
                when 2 then 2
                orElse it * fact(it - 1)
            }
        }
        
        assert fact("5") == 120
'''
    }

    public void testMatchVariable() {
        assertScript '''
        def matcher(num) {
            return match(num) {
                when 1 or 2 or 3 then it * it
                orElse it
            }
        }
         
        assert matcher(1) == 1
        assert matcher(2) == 4
        assert matcher(3) == 9
        assert matcher(10) == 10
'''
    }

    public void testCascadeMatch() {
        assertScript '''
        def matcher(num) {
            return match(num) {
                when 1 or 2 or 3 then it * it
                orElse match(it) {
                        when 4 then 10
                        orElse it
                    }
            }
        }
         
        assert matcher(1) == 1
        assert matcher(2) == 4
        assert matcher(3) == 9
        assert matcher(4) == 10
        assert matcher(10) == 10
'''
    }

    public void testMultiMatch() {
        assertScript '''
        def matcher(int num) {
            return match(num) {
                when 0 or 1 or 2 or 3 then 1
                when 4 or 5 then 2
                orElse 3
            }
        }
        
        assert matcher(0) == 1
        assert matcher(1) == 1
        assert matcher(2) == 1
        assert matcher(3) == 1
        
        assert matcher(4) == 2
        assert matcher(5) == 2
        
        assert matcher(6) == 3
        assert matcher(100500) == 3
        assert matcher(-1) == 3
'''
    }

    public void testClassMatch() {
        assertScript '''
        def matcher(Object it) {
            return match(it) {
                when String or Integer then "string or integer"
                when Date then "date"
                orElse "unknown type"
            }
        }
         
        assert matcher("") == "string or integer"
        assert matcher(1) == "string or integer"
        assert matcher(String) == "string or integer"
        assert matcher(new Date()) == "date"
        assert matcher(1L) == "unknown type"
'''
    }

    public void testRangeMatch() {
        assertScript '''
        def matcher(int num) {
            return match(num) {
                when 0..3 then 1
                when 4 or 5 then 2
                orElse 3
            }
        }
        
        assert matcher(0) == 1
        assert matcher(1) == 1
        assert matcher(2) == 1
        assert matcher(3) == 1
        
        assert matcher(4) == 2
        assert matcher(5) == 2
        
        assert matcher(6) == 3
        assert matcher(100500) == 3
        assert matcher(-1) == 3
'''
    }

    public void testClosureMatch() {
        assertScript '''
        def matcher(Date date) {
            return date.match {
                when { after(new Date("2007/1/2")) } then "after Groovy"
                orElse "before Groovy"
            }
        }
        
        assert matcher(new Date(0)) == "before Groovy"
        assert matcher(new Date()) == "after Groovy"
'''
    }

    public void testClosureMatchInClosure() {
        assertScript '''
        def matcher = { Date date ->
            return date.match {
                when { it.after(new Date("2007/1/2")) } then "after Groovy"
                orElse "before Groovy"
            }
        }
        
        assert matcher(new Date(0)) == "before Groovy"
        assert matcher(new Date()) == "after Groovy"
'''
    }
}
