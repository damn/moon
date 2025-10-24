(ns clojure.vis-ui.scroll-pane
  (:import (com.kotcrab.vis.ui.widget VisScrollPane)))

(defn create [actor]
  (doto (VisScrollPane. actor)
    (.setFlickScroll false)
    (.setFadeScrollBars false)))
