(ns clojure.com.badlogic.gdx.utils.screen-utils
  "Class with static helper methods related to currently bound OpenGL frame buffer, including access to the current OpenGL
  FrameBuffer. These methods can be used to get the entire screen content or a portion thereof."
  (:import (com.badlogic.gdx.utils ScreenUtils)))

(defn clear
  "Clears the color buffers with the specified color."
  [r g b a]
  (ScreenUtils/clear r g b a))
