(ns editor.ui.util
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn find-ancestor [a pred?]
  (loop [actor a]
    (if-let [p (Actor/.getParent actor)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))
