(ns clojure.editor.create-widget-s-boolean
  (:require [clojure.checkbox :as checkbox]
            [clojure.editor.create-widget :refer [create-widget]]))

(defmethod create-widget :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (doto (checkbox/new "" skin)
    (checkbox/set-checked! checked?)))
