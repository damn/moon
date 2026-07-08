(ns clojure.editor.ctx-from-app)

(defn f [^com.badlogic.gdx.Application app]
  {:ctx/audio (.getAudio app)
   :ctx/files (.getFiles app)
   :ctx/graphics (.getGraphics app)
   :ctx/input (.getInput app)})
