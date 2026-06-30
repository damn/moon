(ns scene2d.ui.text-button
  (:require [clojure.gdx :as gdx]))

(defn create
  [{:keys [text skin]}]
  (gdx/text-button ^String text ^Skin skin))
