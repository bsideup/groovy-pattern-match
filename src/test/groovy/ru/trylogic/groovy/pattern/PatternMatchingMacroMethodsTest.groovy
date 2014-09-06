package ru.trylogic.groovy.pattern

import groovy.transform.NotYetImplemented

class PatternMatchingMacroMethodsTest extends GroovyTestCase {

    public void testMatchFact() {
        assertScript '''
        def fact(num) {
            return match(num) {
                String >> fact(num.toInteger())
                (0 | 1) >> 1
                2 >> 2
                _ >> _ * fact(_ - 1)
            }
        }
        
        assert fact("5") == 120
'''
    }

    public void testMatchVariable() {
        assertScript '''
        def matcher(num) {
            return match(num) {
                (1 | 2 | 3) >> _ * _
                _ >> _
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
                (1 | 2 | 3) >> _ * _
                _ >> match(_) {
                        4 >> 10
                        _ >> _
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
                (0 | 1 | 2 | 3) >> 1
                (4 | 5) >> 2
                _ >> 3
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
                (String | Integer) >> "string or integer"
                Date >> "date"
                _ >> "unknown type"
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
                0..3 >> 1
                (4 | 5) >> 2
                _ >> 3
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
                { _ -> _.after(new Date("2007/1/2")) } >> "after Groovy"
                _ >> "before Groovy"
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
                { _ -> _.after(new Date("2007/1/2")) } >> "after Groovy"
                _ >> "before Groovy"
            }
        }
        
        assert matcher(new Date(0)) == "before Groovy"
        assert matcher(new Date()) == "after Groovy"
'''
    }
}
