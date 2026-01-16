(ns moon.info)

(defmulti text (fn [object world]
                 (cond (:item/slot object)
                       :info/item
                       :else
                       :info/entity)))

