(ns clojure.gdx.ui.widget
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]))

(defn create [{:keys [draw!]}]
  (widget/create draw!))
