(ns moon.inventory)

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

(defn can-pickup-item? [inventory item]
  (assert-valid-item? item)
  (or
   (free-cell inventory (:item/slot item)   item)
   (free-cell inventory :inventory.slot/bag item)))
