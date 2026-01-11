(ns moon.inventory
  (:require [clojure.grid2d :as g2d]))

(defprotocol Inventory
  (can-pickup-item? [_ item]))

(def empty-inventory
  #:inventory.slot{:bag      [6 4]
                   :weapon   [1 1]
                   :shield   [1 1]
                   :helm     [1 1]
                   :chest    [1 1]
                   :leg      [1 1]
                   :glove    [1 1]
                   :boot     [1 1]
                   :cloak    [1 1]
                   :necklace [1 1]
                   :rings    [2 1]})

(def valid-slots
  #{:inventory.slot/bag
    :inventory.slot/weapon
    :inventory.slot/shield
    :inventory.slot/helm
    :inventory.slot/chest
    :inventory.slot/leg
    :inventory.slot/glove
    :inventory.slot/boot
    :inventory.slot/cloak
    :inventory.slot/necklace
    :inventory.slot/rings})

(defn valid-slot? [[slot _] item]
  (or (= :inventory.slot/bag slot)
      (= (:item/slot item) slot)))

(defn applies-modifiers? [[slot _]]
  (not= :inventory.slot/bag slot))

(defn stackable? [item-a item-b]
  (and (:count item-a)
       (:count item-b) ; this is not required but can be asserted, all of one name should have count if others have count
       (= (:property/id item-a) (:property/id item-b))))

(defn- cells-and-items [inventory slot]
  (assert (valid-slots slot) (str "Slot :" (pr-str slot)))
  (for [[position item] (slot inventory)]
    [[slot position] item]))

(defn- free-cell [inventory slot item]
  (assert (valid-slots slot) (str "Slot :" (pr-str slot)))
  (first (filter (fn [[_cell cell-item]]
                   (or (stackable? item cell-item)
                       (nil? cell-item)))
                 (cells-and-items inventory slot))))

(defn assert-valid-item? [item]
  (assert (:item/slot item)
          (str "Item not valid: " (pr-str item))))

(extend-type clojure.lang.PersistentHashMap
  Inventory
  (can-pickup-item? [inventory item]
    (assert-valid-item? item)
    (or
     (free-cell inventory (:item/slot item)   item)
     (free-cell inventory :inventory.slot/bag item))))

(defn create! [items eid _world]
  (cons [:tx/assoc eid :entity/inventory (->> empty-inventory
                                              (map (fn [[slot [width height]]]
                                                     [slot (g2d/create-grid width height (constantly nil))]))
                                              (into {}))]
        (for [item items]
          [:tx/pickup-item eid item])))
