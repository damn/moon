(ns editor.create-widget.boolean
  (:require [clojure.checkbox :as checkbox]))

(defn f
  [_ checked? {:keys [ctx/skin]}]
  (doto (checkbox/new "" skin)
    (checkbox/set-checked! checked?)))
