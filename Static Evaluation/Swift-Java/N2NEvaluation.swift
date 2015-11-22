//
//  N2NEvaluation.swift
//  BTLE
//
//  Created by Sanchit Chadha on 11/20/15.
//  Copyright © 2015 Sanchit Chadha. All rights reserved.
//

import Foundation

class N2NEvaluation {
	
	/*
	* Compares this string to the specified object. The result is true if and
	* only if the argument is not null and is a String object that represents
	* the same sequence of characters as this object.
	*/
	func equals_Wrapper(A: String) -> Bool {
		print("equals_Wrapper testing")
		let equalsString:String = ""
		return equalsString == A
	}

	/*
	* This object (which is already a string!) is itself returned.
	*/
	func toString_Wrapper(A: String) -> String {
		print("toString_Wrapper testing")
		let toString:String = A
		return toString;
	}

	/*
	* Compares two strings lexicographically. The comparison is based on the
	* Unicode value of each character in the strings. The character sequence
	* represented by this String object is compared lexicographically to the
	* character sequence represented by the argument string. The result is a
	* negative integer if this String object lexicographically precedes the
	* argument string. The result is a positive integer if this String object
	* lexicographically follows the argument string. The result is zero if the
	* strings are equal; compareTo returns 0 exactly when the equals(Object)
	* method would return true.
	*/
	func compareTo_Wrapper(A: String, B: String) -> Int {
		print("compareTo_Wrapper testing")
		let compareToString:String = A
		return compareToString.compare(B).rawValue
	}
	
	/*
	* Returns the index within this string of the first occurrence of the
	* specified substring, starting at the specified index.
	*/
	func indexOf_Wrapper(A: String, B: String, C: Int) -> String.Index! {
		print("indexOfStartingAt_Wrapper testing")
		let indexOfStartingAtString:String = A
		return indexOfStartingAtString.substringToIndex(indexOfStartingAtString.startIndex.advancedBy(C)).rangeOfString(B)?.startIndex
	}
	
	/*
	* Returns the index within this string of the first occurrence of the
	* specified substring.
	*/
	func indexOf_Wrapper(A: String, B: String) -> Int {
		print("indexOfSubstring_Wrapper testing")
		let indexOfStartingAtString:String = A
		let range = indexOfStartingAtString.rangeOfString(B)
		return indexOfStartingAtString.startIndex.distanceTo(range!.startIndex)
	}
	
	/*
	* Returns the string representation of the String argument.
	*/
	func valueOf_Wrapper(A: String) -> String {
		print("valueOfString_Wrapper testing")
		let valueOfString:String = A
		return valueOfString
	}
	
	/*
	* Returns the string representation of the Boolean argument.
	*/
	func valueOf_Wrapper(A: Bool) -> String {
		print("valueOfBoolean_Wrapper testing")
		let valueOfBoolean:Bool = A
		return String(valueOfBoolean)
	}
	
	/*
	* Returns the string representation of the Integer argument.
	*/
	func valueOf_Wrapper(A: Int) -> String {
		print("valueOfInteger_Wrapper testing")
		let valueOfInteger:Int = A
		return String(valueOfInteger)
	}
	
	/*
	* Returns the string representation of the Double argument.
	*/
	func valueOf_Wrapper(A: Double) -> String {
		print("valueOfDouble_Wrapper testing")
		let valueOfDouble:Double = A
		return String(valueOfDouble)
	}
	
	/*
	* Returns the length of this string. The length is equal to the number of
	* Unicode code units in the string.
	*/
	func length_Wrapper(A: String) -> Int {
		print("length_Wrapper testing")
		let lengthString:String = A
		return lengthString.characters.count
	}
	
	/*
	* Returns true if, and only if, length() is 0.
	*/
	func isEmpty_Wrapper(A: String) -> Bool {
		print("isEmpty_Wrapper testing")
		let isEmptyString:String = A
		return isEmptyString.isEmpty
	}
	
	/*
	* Returns the char value at the specified index. An index ranges from 0 to
	* length() - 1. The first char value of the sequence is at index 0, the
	* next at index 1, and so on, as for array indexing.
	*/
	func charAt_Wrapper(A: String, B: Int) -> Character {
		print("charAt_Wrapper testing")
		let charAtString:String = A
		
		let index = charAtString.startIndex.advancedBy(B)
		return charAtString[index]
	}
	
	/*
	* Compares this String to another String, ignoring case considerations. Two
	* strings are considered equal ignoring case if they are of the same length
	* and corresponding characters in the two strings are equal ignoring case.
	*/
	func equalsIgnoreCase_Wrapper(A: String, B: String) -> Bool {
		print("equalsIgnoreCase_Wrapper testing")
		let equalsIgnoreCaseString:String = A
		return equalsIgnoreCaseString.lowercaseString == B.lowercaseString
	}
	
	/*
	* Compares two strings lexicographically, ignoring case differences. This
	* method returns an integer whose sign is that of calling compareTo with
	* normalized versions of the strings where case differences have been
	* eliminated by calling
	* Character.toLowerCase(Character.toUpperCase(character)) on each
	* character.
	*/
	func compareToIgnoreCase_Wrapper(A: String, B: String) -> Int {
		print("compareToIgnoreCase_Wrapper testing")
		let compareToIgnoreCaseString:String = A
		return compareToIgnoreCaseString.lowercaseString.compare(B.lowercaseString).rawValue
	}
	
	/*
	* Tests if the substring of this string beginning at the specified index
	* starts with the specified prefix.
	*/
	func startsWith_Wrapper(A: String, B: String, C: Int) -> Bool {
		print("startsWith_index_Wrapper testing")
		let startsWithIndexString:String = A
		return startsWithIndexString.substringFromIndex(startsWithIndexString.startIndex.advancedBy(C)).containsString(B)
	}
	
	/*
	* Tests if this string starts with the specified prefix.
	*/
	func startsWith_Wrapper(A: String, B: String) -> Bool {
		print("startsWith_Wrapper testing")
		let startsWithString:String = A
		return startsWithString.substringToIndex(B.endIndex).containsString(B)
	}
	
	/*
	* Tests if this string ends with the specified suffix.
	*/
	func endsWith_Wrapper(A: String, B: String) -> Bool {
		print("endsWith_Wrapper testing")
		let endsWithString:String = A
		return endsWithString.substringFromIndex(B.endIndex).containsString(B)
	}
	
	/*
	* Returns a new string that is a substring of this string. The substring
	* begins at the specified beginIndex and extends to the character at index
	* endIndex - 1. Thus the length of the substring is endIndex-beginIndex.
	*/
	func substring_Wrapper(A: String, B: Int, C: Int) -> String {
		print("substring_begins_ends_Wrapper testing")
		let substringBeginsEndsString:String = A
		return substringBeginsEndsString.substringFromIndex(substringBeginsEndsString.startIndex.advancedBy(B)).substringToIndex(substringBeginsEndsString.startIndex.advancedBy(C))
	}
	
	/*
	* Returns a new string that is a substring of this string. The substring
	* begins with the character at the specified index and extends to the end
	* of this string.
	*/
	func substring_Wrapper(A: String, B: Int) -> String {
		print("substring_begins_Wrapper testing")
		let substringBeginsString:String = A
		return substringBeginsString.substringFromIndex(substringBeginsString.startIndex.advancedBy(B))
	}
	
	/*
	* Concatenates the specified string to the end of this string.
	*
	* If the length of the argument string is 0, then this String object is
	* returned. Otherwise, a new String object is created, representing a
	* character sequence that is the concatenation of the character sequence
	* represented by this String object and the character sequence represented
	* by the argument string.
	*/
	func concat_Wrapper(A: String, B: String) -> String {
		print("concat_Wrapper testing")
		let concatString:String = A
		return concatString.stringByAppendingString(B)
	}
	
	/*
	* Replaces the first substring of this string that matches the given
	* regular expression with the given replacement.
	*/
	func replaceFirst_Wrapper(A: String, B: String, C: String) -> String {
		print("replaceFirst_Wrapper testing")
		var replaceFirstString:String = A
		replaceFirstString.replaceRange(replaceFirstString.rangeOfString(B)!, with: C)
		return replaceFirstString
	}
	
	/*
	* Replaces each substring of this string that matches the given regular
	* expression with the given replacement.
	*/
	func replaceAll_Wrapper(A: String, B: String, C: String) -> String {
		print("replaceAll_Wrapper testing")
		var replaceAllString:String = A
		replaceAllString.replaceRange(replaceAllString.rangeOfString(B)!, with: C)
		return replaceAllString
	}
	
	/*
	* Converts all of the characters in this String to lower case using the
	* rules of the default locale. This is equivalent to calling
	* toLowerCase(Locale.getDefault()).
	*/
	func toLowerCase_Wrapper(A: String) -> String {
		print("toLowerCase_Wrapper testing")
		let toLowerCaseString:String = A
		return toLowerCaseString.lowercaseString;
	}
	
	/*
	* Converts all of the characters in this String to upper case using the
	* rules of the default locale. This method is equivalent to
	* toUpperCase(Locale.getDefault()).
	*/
	func toUpperCase_Wrapper(A: String) -> String {
		print("toUpperCase_Wrapper testing")
		let toUpperCaseString:String = A
		return toUpperCaseString.uppercaseString;
	}
	
	/*
	* Returns a copy of the string, with leading and trailing whitespace
	* omitted.
	*/
	func trim_Wrapper(A: String) -> String {
		print("trim_Wrapper testing")
		let trimString:String = A
		return trimString.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet())
	}
	
	/*
	* Inserts the specified element at the specified position in this list.
	* Shifts the element currently at that position (if any) and any subsequent
	* elements to the right (adds one to their indices).
	*/
	func add_Wrapper(A: [String], B: Int, C: String) -> [String] {
		print("addToIndex_Wrapper testing")
		var addToIndexArrayList:[String] = A
		addToIndexArrayList.insert(C, atIndex: B)
		return addToIndexArrayList;
	}
	
	/*
	* Appends the specified element to the end of this list.
	*/
	func add_Wrapper(A: [String], B: String) -> [String] {
		print("addToEnd_Wrapper testing")
		var addToEndArrayList:[String] = A
		addToEndArrayList.append(B)
		return addToEndArrayList;
	}
	
	/*
	* Removes the element at the specified position in this list. Shifts any
	* subsequent elements to the left (subtracts one from their indices).
	*/
	func remove_Wrapper(A: [String], B: Int) -> [String] {
		print("removeAtIndex_Wrapper testing")
		var removeAtIndexArrayList:[String] = A
		removeAtIndexArrayList.removeAtIndex(B)
		return removeAtIndexArrayList
	}
	
	/*
	* Removes the first occurrence of the specified element from this list, if
	* it is present.
	*/
	func remove_Wrapper(A: [String], B: String) -> [String] {
		print("remove_Wrapper testing")
		var removeArrayList:[String] = A
		removeArrayList.removeAtIndex(removeArrayList.indexOf(B)!)
		return removeArrayList;
	}
	
	/*
	* Returns the element at the specified position in this list.
	*/
	func get_Wrapper(A: [String], B: Int) -> String {
		print("get_Wrapper testing")
		var getArrayList:[String] = A
		return getArrayList[B]
	}
	
	/*
	* Returns a shallow copy of this ArrayList instance. (The elements
	* themselves are not copied.)
	*/
	func clone_Wrapper(A: [String]) -> [String] {
		print("clone_Wrapper testing")
		let cloneArrayList:[String] = A
		let duplicateArrayList = cloneArrayList
		return duplicateArrayList
	}
	
	/*
	* Returns the index of the first occurrence of the specified element in
	* this list, or -1 if this list does not contain the element.
	*/
	func indexOf_Wrapper(A: [String], B: String) -> Int {
		print("indexOf_Wrapper testing")
		let indexOfArrayList:[String] = A
		return indexOfArrayList.indexOf(B)!
	}
	
	/*
	* Removes all of the elements from this list. The list will be empty after
	* this call returns.
	*/
	func clear_Wrapper(A: [String]) -> [String] {
		print("clear_Wrapper testing")
		var clearArrayList:[String] = A
		clearArrayList.removeAll()
		return clearArrayList
	}
	
	/*
	* Returns true if this list contains no elements.
	*/
	func isEmpty_Wrapper(A: [String]) -> Bool {
		print("isEmpty_Wrapper testing")
		let isEmptyArrayList:[String] = A
		return isEmptyArrayList.isEmpty
	}
	
	/*
	* Returns the index of the last occurrence of the specified element in this
	* list, or -1 if this list does not contain the element. More formally,
	* returns the highest index i such that (o==null ? get(i)==null :
	* o.equals(get(i))), or -1 if there is no such index.
	*/
	func lastIndexOf_Wrapper(A: [String], B: String) -> Int {
		print("lastIndexOf_Wrapper testing")
		let lastIndexOfArrayList:[String] = A
		return lastIndexOfArrayList.indexOf(B)!
	}
	
	/*
	* Returns true if this list contains the specified element
	*/
	func contains_Wrapper(A: [String], B: String) -> Bool {
		print("contains_Wrapper testing")
		let containsArrayList:[String] = A
		return containsArrayList.contains(B)
	}
	
	/*
	* Returns the number of elements in this list.
	*/
	func size_Wrapper(A: [String]) -> Int {
		print("size_Wrapper testing")
		let sizeArrayList:[String] = A
		return sizeArrayList.count
	}
	
	/*
	* Returns a view of the portion of this list between the specified
	* fromIndex, inclusive, and toIndex, exclusive. (If fromIndex and toIndex
	* are equal, the returned list is empty.) The returned list is backed by
	* this list, so non-structural changes in the returned list are reflected
	* in this list, and vice-versa. The returned list supports all of the
	* optional list operations.
	*/
	func subList_Wrapper(A: [String], B: Int, C: Int) -> [String] {
		print("subList_Wrapper testing")
		let subListArrayList:[String] = A
		return subListArrayList.subList(B, C);
	}
	
	/*
	* Appends all of the elements in the specified collection to the end of
	* this list, in the order that they are returned by the specified
	* collection's Iterator. The behavior of this operation is undefined if the
	* specified collection is modified while the operation is in progress.
	* (This implies that the behavior of this call is undefined if the
	* specified collection is this list, and this list is nonempty.)
	*/
	func addAll_Wrapper(A: [String], B: [String]) -> Bool {
		print("addAll_Wrapper testing")
		var addAllArrayList:[String] = A
		for item in B {
			addAllArrayList.append(item)
		}
		return true
	}
	
	/*
	* Inserts all of the elements in the specified collection into this list,
	* starting at the specified position. Shifts the element currently at that
	* position (if any) and any subsequent elements to the right (increases
	* their indices). The new elements will appear in the list in the order
	* that they are returned by the specified collection's iterator.
	*/
	func addAll_Wrapper(A: [String], B: Int, C: [String]) -> Bool {
		print("addAllAtIndex_Wrapper testing")
		let addAllAtIndexArrayList:[String] = A
		var result:[String]
		for (index1,element1) in addAllAtIndexArrayList.enumerate() {
			if index1 != B {
				result.append(element1)
			} else {
				for element2 in C {
					result.append(element2)
				}
			}
		}
		return true
	}
	
	/*
	* Replaces the element at the specified position in this list with the
	* specified element.
	*/
	func set_Wrapper(A: [String], B: Int, C: String) -> [String] {
		print("set_Wrapper testing")
		var setArrayList:[String] = A
		setArrayList[B] = C
	}
	
	/*
	* Increases the capacity of this ArrayList instance, if necessary, to
	* ensure that it can hold at least the number of elements specified by the
	* minimum capacity argument.
	*/
	func ensureCapacity_Wrapper(A: [String], B: Int) -> [String] {
		print("ensureCapacity_Wrapper testing")
		let ensureCapacityArrayList:[String] = A
		ensureCapacityArrayList.ensureCapacity(B);
		return ensureCapacityArrayList;
	}
	
	/*
	* Trims the capacity of this ArrayList instance to be the list's current
	* size. An application can use this operation to minimize the storage of an
	* ArrayList instance.
	*/
	func trimToSize_Wrapper(A: [String]) -> [String] {
		print("trimToSize_Wrapper testing")
		let trimToSizeArrayList:[String] = A
		trimToSizeArrayList.trimToSize();
		return trimToSizeArrayList;
	}
	
	/*
	* Removes from this list all of its elements that are contained in the
	* specified collection.
	*/
	func removeAll_Wrapper(A: [String], B: [String]) -> [String] {
		print("removeAll_Wrapper testing")
		var removeAllArrayList:[String] = A
		for element in B {
			if (removeAllArrayList.contains(element)) {
				removeAllArrayList.removeAtIndex(removeAllArrayList.indexOf(element)!)
			}
		}
		return removeAllArrayList;
	}
	
	/*
	* Retains only the elements in this list that are contained in the
	* specified collection. In other words, removes from this list all of its
	* elements that are not contained in the specified collection.
	*/
	func retainAll_Wrapper(A: [String], B: [String]) -> [String] {
		print("retainAll_Wrapper testing")
		var retainAllArrayList:[String] = A
		for element in B {
			if (!retainAllArrayList.contains(element)) {
				retainAllArrayList.removeAtIndex(retainAllArrayList.indexOf(element)!)
			}
		}
		return retainAllArrayList;
	}
	
	/*
	* Returns a list iterator over the elements in this list (in proper
	* sequence), starting at the specified position in the list
	*/
	func listIterator_Wrapper(A: [String], B: Int) -> [String] {
		print("listIteratorFromIndex_Wrapper testing")
		let listIteratorFromIndexArrayList:[String] = A
		listIteratorFromIndexArrayList.listIterator(B);
		return listIteratorFromIndexArrayList;
	}
	
	/*
	* Returns a list iterator over the elements in this list (in proper
	* sequence).
	*/
	func listIterator_Wrapper(A: [String]) -> [String] {
		print("listIterator_Wrapper testing")
		let listIteratorArrayList:[String] = A
		listIteratorArrayList.listIterator();
		return listIteratorArrayList;
	}
	

}