(ns clojure.gdx.scene2d.ui.select-box
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]))

(defn create [{:keys [items selected skin]}]
  (doto (select-box/create skin)
    (select-box/set-items! items)
    (select-box/set-selected! selected)))
