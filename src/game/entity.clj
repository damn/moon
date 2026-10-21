(ns game.entity
  (:require [moon.inventory :as inventory]
            [moon.stats :as stats]
            [moon.timer :as timer]))

(defn add-text-effect [entity elapsed-time text duration]
  (assoc entity :entity/string-effect
         (if-let [existing (:entity/string-effect entity)]
           (-> existing
               (update :text str "\n" text)
               (update :counter timer/increment duration))
           {:text text
            :counter (timer/create elapsed-time duration)})))

(defn- inventory-cell-path [[slot [x y]]]
  [:entity/inventory slot x y])

(defn set-item [entity cell item]
  (assert (and (nil? (get-in entity (inventory-cell-path cell)))
               (inventory/valid-slot? cell item)))
  (cond-> (assoc-in entity (inventory-cell-path cell) item)
    (inventory/applies-modifiers? cell)
    (update :entity/stats stats/add-mods (:stats/modifiers item))))

(defn remove-item [entity cell]
  (let [item (get-in entity (inventory-cell-path cell))]
    (assert item)
    (cond-> (assoc-in entity (inventory-cell-path cell) nil)
      (inventory/applies-modifiers? cell)
      (update :entity/stats stats/remove-mods (:stats/modifiers item)))))
