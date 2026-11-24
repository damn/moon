(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.widget
  "An [[clojure.com.badlogic.gdx.scenes.scene2d.actor]] that participates in layout and provides a minimum, preferred, and maximum size.
  
  The default preferred size of a widget is 0 and this is almost always overridden by a subclass. The default minimum size
  returns the preferred size, so a subclass may choose to return 0 if it wants to allow itself to be sized smaller. The default
  maximum size is 0, which means no maximum size.
  
  See [[clojure.com.badlogic.gdx.scenes.scene2d.utils.layout]] for details on how a widget should participate in layout. A widget's mutator methods should call
  invalidate or invalidate hierarchy as needed."
  (:import (com.badlogic.gdx.scenes.scene2d.ui Widget)))

(defn create [{:keys [draw!]}]
  (proxy [Widget] []
    (draw [batch parent-alpha]
      (draw! this batch parent-alpha))))
