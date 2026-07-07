(ns clojure.widget-value-one-to-one
  (:require [clojure.group :as group]
            [clojure.actor :as actor]))

(defn f [_  widget _schemas]
  (->> (group/get-children widget)
       (keep actor/get-user-object)
       first))
