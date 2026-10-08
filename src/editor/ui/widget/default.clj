(ns editor.ui.widget.default
  (:require [moon.string :as string])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label)))

(defn value [widget]
  ((.getUserObject ^Actor widget) 1))

(defn create [state v]
  (Label. ^String (string/truncate (binding [*print-level* nil]
                                     (pr-str v))
                                   60)
          (:skin @state)))
