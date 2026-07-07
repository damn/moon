(ns clojure.widget-value-default
  (:require [clojure.actor :as actor]))

(defn f
  [_  widget _schemas]
  ((actor/get-user-object widget) 1))
