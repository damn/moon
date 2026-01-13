(ns moon.ui.text-button
  (:import (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (moon Stage)))

; Assumption! We are using 'moon.Stage'
; actually the on-changed can just return [event actor]
; and everytime (.ctx??)?

(defn create
  "Creates a `com.badlogic.gdx.scenes.scene2d.ui.TextButton` with the following options:

  * `text`: `String`.
  * `skin`: `com.badlogic.gdx.scenes.scene2d.ui.Skin`
  * `on-clicked`: `(fn [actor ctx])` for side-effects."
  [{:keys [text
           on-clicked
           ^Skin skin]}]
  (doto (TextButton. (str text) skin)
    (.addListener
     (proxy [ChangeListener] []
       (changed [^Event event actor]
         ; TODO does too much - just pass event,actor ... /// ... ///
         ;; then can just be a 'clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button' namespace?
         (on-clicked actor (.ctx ^Stage (.getStage event))))))))
