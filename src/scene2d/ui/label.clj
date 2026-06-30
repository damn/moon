(ns scene2d.ui.label
  (:require [clojure.gdx :as gdx]))

(defn create
  [{:keys [text skin]}]
  (gdx/label ^String text ^Skin skin))
