(ns editor.create-widget.boolean
  (:require [clojure.gdx.checkbox.new :as new-checkbox]
            [clojure.gdx.checkbox.set-checked :as set-checked]))

(defn f
  [_ checked? {:keys [ctx/skin]}]
  (doto (new-checkbox/f "" skin)
    (set-checked/f checked?)))
