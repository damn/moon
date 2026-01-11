(ns moon.app)

(defprotocol App
  (graphics [_])
  (files [_])
  (audio [_])
  (input [_]))

(extend-type com.badlogic.gdx.Application
  App
  (graphics [this]
    (.getGraphics this))
  (files [this]
    (.getFiles this))
  (audio [this]
    (.getAudio this))
  (input [this]
    (.getInput this)))
