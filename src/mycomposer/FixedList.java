package mycomposer;

import java.util.LinkedList;

/**
 * Contains a linked list with a given maximum length. All new elements are added to the front of
 * the list. If adding a new element causes the length of the list to exceed the maximum length, the
 * element at the end of the list is removed.
 *
 * @param <T> the type of the objects stored in the list
 */
public final class FixedList<T> {

  private final int maxSize;
  private final LinkedList<T> linkedList;

  /**
   * Creates a new fixed-size list with the given maximum size.
   *
   * @param maxSize the maximum size that the list can be
   * @throws IllegalArgumentException if the given maximum size is less than 1
   */
  public FixedList(int maxSize) throws IllegalArgumentException {
    if (maxSize < 1) {
      throw new IllegalArgumentException("Maximum size must be at least 1.");
    }

    this.maxSize = maxSize;
    this.linkedList = new LinkedList<>();
  }

  /**
   * Adds the given item to the front of this list. If adding the given item causes this list to be
   * longer than the maximum length, the item at the end of this list is removed.
   *
   * @param item the item to add to this list
   */
  public void addToList(T item) {
    this.linkedList.remove(item);

    if (this.linkedList.size() >= this.maxSize) {
      this.linkedList.removeLast();
    }

    this.linkedList.addFirst(item);
  }

  /**
   * Gets the size of this list.
   *
   * @return the number of elements in this list
   */
  public int size() {
    return this.linkedList.size();
  }

  /**
   * Returns the given item from this list if it is present.
   *
   * @param item the item to remove if present
   */
  public void remove(T item) {
    this.linkedList.remove(item);
  }

  /**
   * Gets the item at the given index.
   *
   * @param index the index of the item to return
   * @return the item at the given index
   * @throws IllegalArgumentException if the index is out of range of this list
   */
  public T get(int index) throws IllegalArgumentException {
    if (index < 0 || index >= this.linkedList.size()) {
      throw new IllegalArgumentException("Index " + index + " is out of range for length " +
          this.linkedList.size() + ".");
    }

    return this.linkedList.get(index);
  }
}
