(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.scroll-pane
  "A group that scrolls a child actor using scrollbars and/or mouse or touch dragging.
  
  The actor is sized to its preferred size. If the actor's preferred width or height is less than the size of this scroll pane,
  it is set to the size of this scroll pane. Scrollbars appear when the actor is larger than the scroll pane.
  
  The scroll pane's preferred size is that of the child actor. At this size, the child actor will not need to scroll, so the
  scroll pane is typically sized by ignoring the preferred size in one or both directions.
  
  If the scroll pane contains widgets that respond to drag, a listener that calls stop in
  touch down can be added to the widgets to prevent the scroll
  pane from panning when set scroll bar touch is true."
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane Skin)))

(defn create
  "- `actor`: May be null."
  [^Actor actor ^Skin skin]
  (ScrollPane. actor skin))
