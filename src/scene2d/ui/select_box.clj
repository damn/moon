(ns scene2d.ui.select-box
  (:require [clojure.gdx :as gdx]))

(defn create
  [{:keys [items selected skin]}]
  (doto (gdx/select-box ^Skin skin)
    (gdx/select-box-set-items! items)
    (gdx/select-box-set-selected! selected)))
