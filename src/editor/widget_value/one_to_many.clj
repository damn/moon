(ns editor.widget-value.one-to-many
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn f
  [_  widget _schemas]
  (->> (Group/.getChildren widget)
       (keep Actor/.getUserObject)
       set))
