(ns clojure.gdx.scene2d.ui.horizontal-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn create [{:keys [space pad]}]
  (let [group (HorizontalGroup.)]
    (when space (.space group (float space)))
    (when pad   (.pad   group (float pad)))
    group))
