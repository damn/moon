(ns scene2d.ui.label
  (:require [clojure.gdx.label.new :as new-label]))

(defn create
  [{:keys [text skin]}]
  (new-label/f text skin))
