(ns editor.ui.widget.animation
  (:require [editor.ui.widget.image :refer [scaled-image-button]]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn create [state animation]
  (let [table (doto (Table.)
                (#(.pad (.defaults ^Table %) (float 1))))]
    (doseq [image (:animation/frames animation)]
      (.add table ^Actor (scaled-image-button
                          (textures/texture-region (:textures @state) image)
                          2)))
    (.row ^Table table)
    (.pack table)
    table))
