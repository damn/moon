(ns clojure.widget-value-one-to-many
  (:require
            [clojure.get-user-object] [clojure.group :as group]))

(defn f
  [_  widget _schemas]
  (->> (group/get-children widget)
       (keep clojure.get-user-object/f)
       set))
