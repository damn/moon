(ns moon.ui.check-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               CheckBox
                                               Skin)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(def checked? Button/.isChecked)

(defn create
  [& {:keys [text on-clicked checked? ^Skin skin]}]
  (let [^Button button (CheckBox. (str text) skin)]
    (.setChecked button checked?)
    (.addListener button
                  (proxy [ChangeListener] []
                    (changed [event ^Button actor]
                      (on-clicked (.isChecked actor)))))
    button))
