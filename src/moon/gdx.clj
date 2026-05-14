(ns moon.gdx)

(defprotocol Gdx
  (fit-viewport [_ world-width world-height camera]))
