(ns moon.item-on-cursor-test
  "Characterization tests for item-on-cursor handlers.
   See docs/item-on-cursor.md for flow diagrams."
  (:require [clojure.k-clicked-inventory-cell.player-idle :as idle-click]
            [clojure.k-clicked-inventory-cell.player-item-on-cursor :as cursor-click]
            [clojure.k-handle-input.player-item-on-cursor :as cursor-input]
            [clojure.k-state-enter.player-item-on-cursor :as cursor-enter]
            [clojure.k-state-exit.player-item-on-cursor :as cursor-exit]
            [clojure.item.place-position :refer [item-place-position]]
            [clojure.test :refer [deftest is testing]]))

(def weapon-cell [:inventory.slot/weapon [0 0]])
(def bag-cell [:inventory.slot/bag [0 0]])

(def sword {:property/id :items/test-sword
            :property/pretty-name "Test Sword"
            :entity/image {}
            :item/slot :inventory.slot/weapon})

(def ring {:property/id :items/test-ring
           :property/pretty-name "Test Ring"
           :entity/image {}
           :item/slot :inventory.slot/rings})

(def stackable-a
  (assoc sword
         :property/id :items/test-potion
         :item/slot :inventory.slot/bag
         :count 2))

(def stackable-b
  (assoc stackable-a :count 3))

(defn inventory-with
  [[slot pos] item]
  {slot {pos item}})

(defn player-eid
  [state inventory & {:keys [item-on-cursor click-distance]}]
  (atom (cond-> {:entity/fsm {:fsm :fsms/player :state state}
                 :entity/inventory inventory
                 :entity/body {:body/position [5 5]}
                 :entity/click-distance-tiles (or click-distance 1.5)}
          item-on-cursor (assoc :entity/item-on-cursor item-on-cursor)
          (= state :player-item-on-cursor)
          (assoc :player-item-on-cursor {:item item-on-cursor}))))

(deftest idle-click-pickup-txs
  (let [eid (player-eid :player-idle (inventory-with weapon-cell sword))]
    (is (= [[:tx/sound "bfxr_takeit"]
            [:tx/event eid :pickup-item sword]
            [:tx/remove-item eid weapon-cell]]
           (idle-click/f eid weapon-cell)))))

(deftest idle-click-empty-cell
  (let [eid (player-eid :player-idle (inventory-with weapon-cell sword))]
    (is (nil? (idle-click/f eid bag-cell)))))

(deftest cursor-enter-assoc-item
  (let [eid (atom {})]
    (is (= [[:tx/assoc eid :entity/item-on-cursor sword]]
           (cursor-enter/f {:item sword} eid)))))

(deftest cursor-click-empty-cell-order
  (testing "dissoc before dropped-item so state-exit does not spawn on ground"
    (let [eid (player-eid :player-item-on-cursor {} :item-on-cursor sword)]
      (is (= [[:tx/sound "bfxr_itemput"]
              [:tx/dissoc eid :entity/item-on-cursor]
              [:tx/set-item eid weapon-cell sword]
              [:tx/event eid :dropped-item]]
             (cursor-click/f eid weapon-cell))))))

(deftest cursor-click-invalid-slot
  (let [eid (player-eid :player-item-on-cursor {} :item-on-cursor sword)]
    ;; ring slot invalid for weapon
    (is (nil? (cursor-click/f eid [:inventory.slot/rings [0 0]])))))

(deftest cursor-click-stack-emits-stack-tx
  (let [eid (player-eid :player-item-on-cursor (inventory-with bag-cell stackable-a)
                        :item-on-cursor stackable-b)]
    (is (= [[:tx/sound "bfxr_itemput"]
            [:tx/dissoc eid :entity/item-on-cursor]
            [:tx/stack-item eid bag-cell stackable-b]
            [:tx/event eid :dropped-item]]
           (cursor-click/f eid bag-cell)))))

(deftest cursor-click-swap
  (let [eid (player-eid :player-item-on-cursor (inventory-with weapon-cell ring)
                        :item-on-cursor sword)]
    (is (= [[:tx/sound "bfxr_itemput"]
            [:tx/dissoc eid :entity/item-on-cursor]
            [:tx/remove-item eid weapon-cell]
            [:tx/set-item eid weapon-cell sword]
            [:tx/event eid :dropped-item]
            [:tx/event eid :pickup-item ring]]
           (cursor-click/f eid weapon-cell)))))

(deftest cursor-exit-spawns-when-item-on-cursor
  (let [eid (player-eid :player-item-on-cursor {} :item-on-cursor sword)]
    (is (= [[:tx/sound "bfxr_itemputground"]
            [:tx/dissoc eid :entity/item-on-cursor]
            [:tx/spawn-item [5 5] sword]]
           (cursor-exit/f {} eid {})))))

(deftest cursor-exit-skips-when-already-cleared
  (testing "inventory path dissoc's before FSM exit"
    (let [eid (player-eid :player-item-on-cursor {} :item-on-cursor nil)]
      (is (nil? (cursor-exit/f {} eid {}))))))

(deftest cursor-input-drop-on-world-click
  (let [eid (atom {})]
    (with-redefs [clojure.ctx-button-just-pressed/button-just-pressed? (constantly true)
                  clojure.mouseover-actor/mouseover-actor (constantly nil)]
      (is (= [[:tx/event eid :drop-item]]
             (cursor-input/f eid {:ctx/input nil}))))))

(deftest cursor-input-no-drop-over-ui
  (let [eid (atom {})
        ui-actor (reify Object)]
    (with-redefs [clojure.ctx-button-just-pressed/button-just-pressed? (constantly true)
                  clojure.mouseover-actor/mouseover-actor (constantly ui-actor)]
      (is (nil? (cursor-input/f eid {:ctx/stage {:stage/root ui-actor}}))))))

(deftest item-place-position-at-feet
  (is (= [5 5]
         (item-place-position {:entity/body {:body/position [5 5]}}))))
