
/**
 * Name: Der Wackeldackel
 * Batch: 2
 * Level: 4
 * Instructions used: 0
 * 
 * Copyright (c) 2020 Max Baumann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import codescape.Dogbot;

public class MyDogbot extends Dogbot {

    public void run() {

        String WORLD_URL = "http://" + System.getenv("WORLD") + "/";
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

        boolean turnAfterMove = false;

        for (int i = 0; i < 8; i++) {
            String r = "";
            int l = 0;
            if (turnAfterMove) {
                turnAfterMove = false;
                l = 2;
            }
            if (i == 4) {
                l = 1;
            }

            for (int part = 0; part < 3 + l; part++) {

                String instr = null;

                if (part == 0) {
                    instr = "pickUp";
                } else if (part < 3) {
                    if (part == 1) {
                        instr = "isMovePossible";
                    } else if (!r.equals("false")) {
                        instr = "move";
                    } else {
                        l = 2;
                        i -= 2;
                        turnAfterMove = true;
                        part++;
                    }
                } else {
                    instr = "turnLeft";
                }

                if (instr != null) {
                    try {
                        final okhttp3.Request request = new okhttp3.Request.Builder().url(WORLD_URL + instr).post(
                                okhttp3.RequestBody.create("", okhttp3.MediaType.get("text/plain; charset=utf-8")))
                                .build();
                        final okhttp3.Response response = client.newCall(request).execute();
                        final String responseBody = (response.body() != null) ? response.body().string() : "";
                        if (response.code() == 410) {
                            Thread.currentThread().stop();
                        }
                        r = responseBody;
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                        r = "";
                    }
                }

            }

        }
    }

}