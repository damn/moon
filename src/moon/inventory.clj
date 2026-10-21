(ns moon.inventory
  (:require [moon.g2d :as g2d]
            [moon.item :as item]))

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
  (let [grid (slot inventory)]
    (for [position (g2d/posis grid)]
      [[slot position] (g2d/get grid position)])))

(defn- free-cell [inventory slot]
  (assert (known-slot? slot) (str "Slot :" (pr-str slot)))
  (first (filter (fn [[_cell cell-item]]
                   (nil? cell-item))
                 (cells-and-items inventory slot))))

(defn can-pickup-item? [inventory item]
  (assert (item/valid? item))
  (or (free-cell inventory (:item/slot item))
      (free-cell inventory :inventory.slot/bag)))

(defn item-at [inventory [slot position]]
  (g2d/get (slot inventory) position))

(defn valid-slot? [[slot _] item]
  (or (= :inventory.slot/bag slot)
      (= (:item/slot item) slot)))

(defn applies-modifiers? [[slot _]]
  (not= :inventory.slot/bag slot))
