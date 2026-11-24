(ns clojure.com.badlogic.gdx.scenes.scene2d.utils.click-listener
  "Detects mouse over, mouse or finger touch presses, and clicks on an actor. A touch must go down over the actor and is
  considered pressed as long as it is over the actor or within the set tap square size. This behavior
  makes it easier to press buttons on a touch interface when the initial touch happens near the edge of the actor. Double clicks
  can be detected using get tap count. Any touch (not just the first) will trigger this listener. While pressed, other
  touch downs are ignored."
  (:import (com.badlogic.gdx.scenes.scene2d.utils ClickListener)))

(defn create [clicked!]
  (proxy [ClickListener] []
    (clicked [event x y]
      (clicked! event x y))))
