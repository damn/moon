(ns scene2d.ui.select-box
  (:require [clojure.gdx.select-box.new :as new-select-box]
            [clojure.gdx.select-box.set-items :as set-items]
            [clojure.gdx.select-box.set-selected :as set-selected]))

(defn create
  [{:keys [items selected skin]}]
  (doto (new-select-box/f skin)
    (set-items/f items)
    (set-selected/f selected)))
