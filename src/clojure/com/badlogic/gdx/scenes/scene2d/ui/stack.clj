(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.stack
  "A stack is a container that sizes its children to its size and positions them at 0,0 on top of each other.
  
  The preferred and min size of the stack is the largest preferred and min size of any children. The max size of the stack is the
  smallest max size of any children."
  (:import (com.badlogic.gdx.scenes.scene2d.ui Stack)))

(defn create []
  (Stack.))
