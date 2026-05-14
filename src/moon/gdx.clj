(ns moon.gdx)

(defprotocol Gdx
  (fit-viewport [_ world-width world-height camera])
  (orthographic-camera [_ {:keys [y-down? world-width world-height]}])
  )
