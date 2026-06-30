(ns scene2d.ui.widget
  (:require [clojure.gdx :as gdx]))

(defn f
  [{:keys [draw!]}]
  (gdx/widget {:draw! draw!}))
