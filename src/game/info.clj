(ns game.info)

(defmulti text (fn [object _ctx]
                 (cond (:item/slot object)
                       :info/item
                       :else
                       :info/entity)))
