(ns scene2d.ui.text-button
  (:require [clojure.gdx.text-button.new :as new-text-button]))

(defn create
  [{:keys [text skin]}]
  (new-text-button/f text skin))
