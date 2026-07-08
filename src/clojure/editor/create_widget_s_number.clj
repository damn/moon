(ns clojure.editor.create-widget-s-number
  (:require [clojure.add-listener]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.edn-str :refer [->edn-str]]
            [clojure.ui-text-field :as text-field]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.tooltip :as tooltip]))

(defmethod create-widget :s/number
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (clojure.add-listener/f (text-tooltip/create (str schema) skin))))
