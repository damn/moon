(ns game.listener.create.ui-actors.action-bar
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup HorizontalGroup Table)))

(defn create-action-bar []
  (let [table (doto (Table.)
                (#(.pad (.defaults ^Table %) (float 2))))]
    (doto (.add ^Table table ^Actor (doto (HorizontalGroup.)
                                      (.space (float 2))
                                      (.pad (float 2))
                                      (.setName "moon.ui.action-bar.horizontal-group")
                                      (.setUserObject (doto (ButtonGroup.)
                                                        (.setMaxCheckCount (int 1))
                                                        (.setMinCheckCount (int 0))))))
      (.expand)
      (.bottom))
    (.row ^Table table)
    (doto table
      (.pack)
      (.setFillParent true)
      (.setName "moon.ui.action-bar"))))
