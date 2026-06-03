(ns clojure.gdx.scene2d.ui.widget-group.set-fill-parent
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

(defn set-fill-parent! [^WidgetGroup widget-group bool]
  (.setFillParent widget-group bool))
