(ns moon.inventory
  (:require [moon.item :as item]))

(def slots
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

(defn- known-slot? [slot]
  (slots slot))

(defn- cells-and-items [inventory slot]
  (assert (known-slot? slot) (str "Slot :" (pr-str slot)))
  (for [[position item] (slot inventory)]
    [[slot position] item]))

(defn- free-cell [inventory slot item]
  (assert (known-slot? slot) (str "Slot :" (pr-str slot)))
  (first (filter (fn [[_cell cell-item]]
                   (or (item/stackable? item cell-item)
                       (nil? cell-item)))
                 (cells-and-items inventory slot))))

(defn can-pickup-item? [inventory item]
  (assert (item/valid? item))
  (or (free-cell inventory (:item/slot item) item)
      (free-cell inventory :inventory.slot/bag item)))

(defn valid-slot? [[slot _] item]
  (or (= :inventory.slot/bag slot)
      (= (:item/slot item) slot)))

(defn applies-modifiers? [[slot _]]
  (not= :inventory.slot/bag slot))
