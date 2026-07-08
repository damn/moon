(ns clojure.editor.create-widget-s-string
  (:require [clojure.actor.add-listener]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.ui-text-field :as text-field]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.tooltip :as tooltip]))

(defmethod create-widget :s/string
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (clojure.actor.add-listener/f (text-tooltip/create (str schema) skin))))
